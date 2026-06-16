import glob
import re

properties_files = glob.glob('d:/Corporate/PROJECTS/Ecommerce-Microservices/config-properties/*.properties')

for filepath in properties_files:
    with open(filepath, 'r') as f:
        content = f.read()

    lines = content.split('\n')
    new_lines = []
    for line in lines:
        if line.startswith('eureka.client.service-url.defaultZone') or line.startswith('eureka.instance.hostname'):
            continue
        new_lines.append(line)
        
    with open(filepath, 'w') as f:
        f.write('\n'.join(new_lines))
    print(f"Updated {filepath}")
