import os
import glob

def remove_strings_from_file(filepath, strings_to_remove):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    modified = False
    for s in strings_to_remove:
        if s in content:
            content = content.replace(s, "")
            modified = True
            
    if modified:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {filepath}")

root_dir = "d:\\Corporate\\PROJECTS\\Ecommerce-Microservices"
strings = [
    "import org.springframework.cloud.netflix.eureka.EnableEurekaClient;\n",
    "import org.springframework.cloud.netflix.eureka.EnableEurekaClient;\r\n",
    "@EnableEurekaClient\n",
    "@EnableEurekaClient\r\n"
]

java_files = glob.glob(os.path.join(root_dir, "**", "*.java"), recursive=True)
for java_file in java_files:
    remove_strings_from_file(java_file, strings)

print("Eureka cleanup script completed.")
