# application-file-server

File server application provides two categories of services.

1. File upload

2. Photo gallery backend support

Photos under albums are responsive and encrypted through NGINX Image Server, more information please see: [NGINX Docs](https://docs.nginx.com/nginx/admin-guide/web-server/serving-static-content/)

Some snippets of my NGINX configurations relating to Image server:

``nginx1.16/conf.d-https/default.conf``

```
...

# Cache settings
proxy_cache_path /tmp/nginx-images-cache/ levels=1:2 keys_zone=my_cache:10m inactive=24h max_size=100m;

server {
...
	# Forbid direct access to img folder
	location /img/ {
	    return 403;
	}
	
	location ~ "^/resize/(?<width>(1080|720|480|320|240))/(?<image>.+)$" {
		alias /usr/share/nginx/html/appimg/$image;

        image_filter resize $width -;
        image_filter_jpeg_quality 95;
        image_filter_buffer 8M;
		proxy_cache my_cache;
		proxy_cache_valid 200 24h;
	}
	
    location ~ "^/media/(?<width>(1080|720|480|320|240))/(?<image>.+)$" {
		alias /usr/share/nginx/html/img/$image;

	    secure_link $arg_hash,$arg_expires;
		secure_link_md5 "$secure_link_expires$uri secret";
		
		if ($secure_link = "") { return 403; }
        if ($secure_link = "0") { return 410; }
		
        image_filter resize $width -;
        image_filter_jpeg_quality 95;
        image_filter_buffer 8M;
		proxy_cache my_cache;
		proxy_cache_valid 200 24h;
	}

	location /resize {
		proxy_pass https://localhost:xxxx/;
	}
	location /media {
		proxy_pass https://localhost:xxxx/;
	}
...
```


#### API Samples

###### Get album list sample

``GET`` ``/photo/albums.do``

response

```
{
    "albums": [
        {
            "name": "动物园及日常",
            "nameByPath": "zoo202106",
            "time": 1623747776000,
            "description": "2021年6月",
            "owner": "hongwei",
            "thumbnail": "https://hongwei-test1.top/resize/320/photo/covers/zoo202106.jpg",
            "numberOfPhotos": 118
        },
        {
            "name": "flightsim",
            "nameByPath": "flightsim",
            "time": 1619719675000,
            "description": "",
            "owner": "",
            "thumbnail": "https://hongwei-test1.top/resize/320/photo/covers/flightsim.jpg",
            "numberOfPhotos": 3
        },
        {
            "name": "兔兔",
            "nameByPath": "tutu",
            "time": 1618456865000,
            "description": "兔兔的相册",
            "owner": "hongwei",
            "thumbnail": "https://hongwei-test1.top/resize/320/photo/covers/tutu.jpg",
            "numberOfPhotos": 4
        },
        {
            "name": "test",
            "nameByPath": "test",
            "time": 1618316974000,
            "description": "",
            "owner": "",
            "thumbnail": "https://hongwei-test1.top/resize/320/photo/covers/test.jpg",
            "numberOfPhotos": 3
        }
    ]
}
```

###### Get album list sample

``GET`` ``/photo/photos.do?album=test&resolution=high``

response

```
{
    "album": "test",
    "res": "High",
    "images": [
        {
            "original": "https://hongwei-test1.top/media/1080/test/IMG_20190508_155740.jpg?expires=1633017600000&hash=mKUiXzlYYlYBUTKNsGeHHg",
            "thumbnail": "https://hongwei-test1.top/media/320/test/IMG_20190508_155740.jpg?expires=1633017600000&hash=6k230-_0uiu18KY9SIgDvA"
        },
        {
            "original": "https://hongwei-test1.top/media/1080/test/IMG_20190505_145103.jpg?expires=1633017600000&hash=hIiWGwuOtvBlDkgNwqGzWQ",
            "thumbnail": "https://hongwei-test1.top/media/320/test/IMG_20190505_145103.jpg?expires=1633017600000&hash=x9VPsgxwFKpp6w5RVfV4NQ"
        },
        {
            "original": "https://hongwei-test1.top/media/1080/test/IMG_20190508_155745.jpg?expires=1633017600000&hash=OqbNOwjFUh2HLjHJYswEiA",
            "thumbnail": "https://hongwei-test1.top/media/320/test/IMG_20190508_155745.jpg?expires=1633017600000&hash=0OPFBF9ldF25VZ17fZzQWw"
        }
    ]
}
```

#### Related projects

Frontend(Web) repository:
[hongwei-bai/react-homepage-demo](https://github.com/hongwei-bai/react-homepage-demo).

Authentication service(backend) repository:
[hongwei-bai/application-service-authentication](https://github.com/hongwei-bai/application-service-authentication)
