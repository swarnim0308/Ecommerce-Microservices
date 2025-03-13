import subprocess
import os
import time

def start_service(service_path):
    os.chdir(service_path)
    subprocess.Popen("mvn spring-boot:run", shell=True)

# Path to the Ecommerce-Microservices directory
root_path = "D:\\Corporate\\PROJECTS\\Ecommerce-Microservices"

# Services in specific order
specific_services = ["service-registery"]
# specific_services = ["service-registery", "config-server"]
# Start specific services first
for service in specific_services:
    service_path = os.path.join(root_path, service)
    if os.path.isdir(service_path):
        start_service(service_path)
        time.sleep(25)  # Adjust sleep time as necessary

# Start all other services
# for service in os.listdir(root_path):
#     service_path = os.path.join(root_path, service)
#     if os.path.isdir(service_path) and service not in specific_services:
#         start_service(service_path)
#         time.sleep(25)  # Optional: wait a few seconds between starting each service
