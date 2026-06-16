import os
import glob
import re

def fix_pom(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    modified = False
    
    # 1. Remove Springfox dependencies
    # Regex to match <dependency>...springfox...</dependency>
    springfox_pattern = re.compile(r'<dependency>\s*<groupId>io\.springfox</groupId>.*?</dependency>', re.DOTALL)
    if springfox_pattern.search(content):
        content = springfox_pattern.sub('', content)
        modified = True
        
    # 2. Add Lombok version property if not present
    if '<properties>' in content and '<lombok.version>' not in content:
        content = content.replace('<properties>', '<properties>\n\t\t<lombok.version>1.18.32</lombok.version>')
        modified = True

    if modified:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Fixed {filepath}")

root_dir = "d:\\Corporate\\PROJECTS\\Ecommerce-Microservices"
pom_files = glob.glob(os.path.join(root_dir, "**", "pom.xml"), recursive=True)

for pom in pom_files:
    if "frontend\\node_modules" in pom: continue
    fix_pom(pom)

print("Comprehensive POM fix completed.")
