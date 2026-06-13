import os
import glob
import re

def clean_springfox(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    modified = False
    
    # Remove lines containing springfox imports or annotations
    lines = content.split('\n')
    new_lines = []
    for line in lines:
        if 'springfox' in line or '@EnableSwagger2' in line:
            modified = True
            continue
        new_lines.append(line)
    
    content = '\n'.join(new_lines)
    
    # Remove Docket bean method (matches @Bean public Docket ... { ... })
    docket_pattern = re.compile(r'@Bean\s+public\s+Docket.*?\{.*?\}', re.DOTALL)
    if docket_pattern.search(content):
        content = docket_pattern.sub('', content)
        modified = True

    if modified:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Cleaned Springfox from {filepath}")

root_dir = "d:\\Corporate\\PROJECTS\\Ecommerce-Microservices"
java_files = glob.glob(os.path.join(root_dir, "**", "*Application.java"), recursive=True)

for java in java_files:
    clean_springfox(java)

print("Springfox cleanup completed.")
