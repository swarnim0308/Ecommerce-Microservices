import glob
import re

properties_files = glob.glob('d:/Corporate/PROJECTS/Ecommerce-Microservices/config-properties/*.properties')

for filepath in properties_files:
    with open(filepath, 'r') as f:
        content = f.read()

    # Find the JDBC url line and replace it
    # E.g. spring.datasource.url=jdbc:postgresql://postgres-db:5432/product
    content = re.sub(r'spring.datasource.url=jdbc:postgresql://postgres-db:5432/.*', 'spring.datasource.url=jdbc:postgresql://postgres-db:5432/ecommerce', content)
        
    with open(filepath, 'w') as f:
        f.write(content)
    print(f"Fixed DB URL in {filepath}")
