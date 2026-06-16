import re

with open('d:/Corporate/PROJECTS/Ecommerce-Microservices/docker-compose.yml', 'r') as f:
    content = f.read()

# Replace EUREKA_CLIENT_SERVICEURL_DEFAULTZONE... with both config uri and eureka uri
replacement = """      - SPRING_CLOUD_CONFIG_URI=http://config-server:8888
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-registry:8761/eureka/"""

content = content.replace("      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-registry:8761/eureka/", replacement)

with open('d:/Corporate/PROJECTS/Ecommerce-Microservices/docker-compose.yml', 'w') as f:
    f.write(content)
