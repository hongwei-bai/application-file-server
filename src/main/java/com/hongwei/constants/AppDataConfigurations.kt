package com.hongwei.constants

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "appdata")
open class AppDataConfigurations {
    lateinit var uploadExercisePath: String
    lateinit var imagesRoot: String
    lateinit var imagesDomain: String
    lateinit var imageLocation: String
    lateinit var imageSecret: String
}