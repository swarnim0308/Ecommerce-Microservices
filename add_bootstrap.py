import os
import re

services = ["Cart-Entity", "CustomerService", "Inventory-Service", "OrderService", "product-service", "shipping-service", "zuul-api-gateway"]
dependency_xml = """
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
        </dependency>
"""

for service in services:
    pom_path = os.path.join("d:/Corporate/PROJECTS/Ecommerce-Microservices", service, "pom.xml")
    if os.path.exists(pom_path):
        with open(pom_path, 'r', encoding='utf-8') as f:
            content = f.read()
            
        if 'spring-cloud-starter-bootstrap' not in content:
            # Insert after the first <dependencies> tag
            content = re.sub(r'(<dependencies>)', r'\1' + dependency_xml, content, count=1)
            with open(pom_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Added bootstrap to {service}")
        else:
            print(f"Bootstrap already in {service}")
