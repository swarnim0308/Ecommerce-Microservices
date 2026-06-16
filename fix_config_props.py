import glob

properties_files = glob.glob('d:/Corporate/PROJECTS/Ecommerce-Microservices/config-properties/*.properties')

for filepath in properties_files:
    with open(filepath, 'r') as f:
        content = f.read()

    # Replace H2 driver with PostgreSQL
    content = content.replace('spring.datasource.driver-class-name=org.h2.Driver', 'spring.datasource.driver-class-name=org.postgresql.Driver')
    
    # Replace H2 URL with PostgreSQL
    content = content.replace('spring.datasource.url=jdbc:h2:mem:', 'spring.datasource.url=jdbc:postgresql://postgres-db:5432/')
    
    # Update username and password for Postgres (as configured in docker-compose)
    content = content.replace('spring.datasource.username=sa', 'spring.datasource.username=root')
    content = content.replace('spring.datasource.password=', 'spring.datasource.password=password')
    
    # Replace Hibernate Dialect
    content = content.replace('spring.jpa.database-platform=org.hibernate.dialect.H2Dialect', 'spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect')
    
    # Replace zuul properties with Spring Cloud Gateway (if any)
    
    with open(filepath, 'w') as f:
        f.write(content)
    print(f"Updated {filepath}")
