import os
import glob
import re

def fix_lombok(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    modified = False
    
    lombok_config = """
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<annotationProcessorPaths>
						<path>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
							<version>1.18.32</version>
						</path>
					</annotationProcessorPaths>
				</configuration>
			</plugin>"""
    
    # Check if maven-compiler-plugin is already defined
    if '<artifactId>maven-compiler-plugin</artifactId>' not in content:
        # Add it inside <plugins>
        if '<plugins>' in content:
            content = content.replace('<plugins>', '<plugins>' + lombok_config)
            modified = True

    if modified:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Added Lombok compiler config to {filepath}")

root_dir = "d:\\Corporate\\PROJECTS\\Ecommerce-Microservices"
pom_files = glob.glob(os.path.join(root_dir, "**", "pom.xml"), recursive=True)

for pom in pom_files:
    if "frontend\\node_modules" in pom: continue
    fix_lombok(pom)

print("Lombok POM fix completed.")
