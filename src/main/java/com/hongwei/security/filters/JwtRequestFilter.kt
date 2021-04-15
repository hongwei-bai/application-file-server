package com.hongwei.security.filters

import com.hongwei.constants.AppTokenExpiredException
import com.hongwei.constants.SecurityConfigurations
import com.hongwei.constants.Unauthorized
import com.hongwei.security.model.SubCodeResponseFactory.genericUnauthorizedError
import com.hongwei.security.model.SubCodeResponseFactory.noPermission
import com.hongwei.security.model.SubCodeResponseFactory.tokenExpired
import com.hongwei.security.service.AuthorisationService
import com.hongwei.security.service.MyUserDetailsService
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtRequestFilter : OncePerRequestFilter() {
    private val _logger: Logger = LogManager.getLogger(JwtRequestFilter::class.java)

    @Autowired
    private lateinit var userDetailsService: MyUserDetailsService

    @Autowired
    private lateinit var authorisationService: AuthorisationService

    @Autowired
    private lateinit var securityConfigurations: SecurityConfigurations

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authorizationHeader = request.getHeader(securityConfigurations.authorizationHeader)

        /*
        The W3 spec for CORS preflight requests clearly states that user credentials should be excluded.
        Reference[a]: https://stackoverflow.com/questions/15734031/why-does-the-preflight-options-request-of-an-authenticated-cors-request-work-in
        Reference[b]: https://fetch.spec.whatwg.org/#cors-protocol-and-credentials
         */
        if (request.method == HttpMethod.OPTIONS.name) {
            grantAccess(request)
        } else if (authorizationHeader != null && authorizationHeader.startsWith(securityConfigurations.authorizationBearer)) {
            val jwt = authorizationHeader.substring(securityConfigurations.authorizationBearer.length + 1)
            try {
                if (SecurityContextHolder.getContext().authentication == null) {
                    if (authorisationService.authorise(jwt, request.method, request.requestURI, request)) {
                        grantAccess(request)
                    } else {
                        /*
                        I had a issue that I always got 403 FORBIDDEN instead of 401 UNAUTHORIZED,
                        The fix is to add an authenticationEntryPoint in WebSecurityConfigurerAdapter::configure callback.
                        Reference: https://stackoverflow.com/questions/49241384/401-instead-of-403-with-spring-boot-2
                        More: https://stackoverflow.com/questions/49497192/spring-boot-2-403-instead-of-401-in-filter-based-jwt-spring-security-implement
                        */
                        response.writer.write(noPermission())
                    }
                }
            } catch (e: AppTokenExpiredException) {
                response.writer.write(tokenExpired())
            } catch (e: Unauthorized) {
                response.writer.write(genericUnauthorizedError())
            }
        }

        appendCORSHeaders(response)
        chain.doFilter(request, response)
    }

    private fun grantAccess(request: HttpServletRequest) {
        val userDetails = userDetailsService.loadUserByUsername("stub")

        // Grant access
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.authorities)
        usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
    }

    private fun appendCORSHeaders(response: HttpServletResponse) {
        response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, true.toString())
        response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "$CONTENT_TYPE,${securityConfigurations.authorizationHeader}")
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, securityConfigurations.corsAllowDomain)
    }
}