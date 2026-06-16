import os
import glob

def replace_in_file(filepath, old_str, new_str):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    if old_str in content:
        content = content.replace(old_str, new_str)
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {filepath}")

root_dir = "d:\\Corporate\\PROJECTS\\Ecommerce-Microservices"

# 1. Update POMs
pom_files = glob.glob(os.path.join(root_dir, "**", "pom.xml"), recursive=True)
for pom in pom_files:
    if "frontend\\node_modules" in pom: continue
    
    replace_in_file(pom, "<version>2.2.6.RELEASE</version>", "<version>3.2.4</version>")
    replace_in_file(pom, "<java.version>11</java.version>", "<java.version>17</java.version>")
    replace_in_file(pom, "<spring-cloud.version>Hoxton.SR3</spring-cloud.version>", "<spring-cloud.version>2023.0.1</spring-cloud.version>")

# 2. Update javax -> jakarta in Java source files
java_files = glob.glob(os.path.join(root_dir, "**", "*.java"), recursive=True)
for java_file in java_files:
    replace_in_file(java_file, "javax.persistence", "jakarta.persistence")
    replace_in_file(java_file, "javax.validation", "jakarta.validation")
    replace_in_file(java_file, "javax.servlet", "jakarta.servlet")
    replace_in_file(java_file, "javax.annotation", "jakarta.annotation")
    replace_in_file(java_file, "javax.transaction", "jakarta.transaction")

print("Migration script completed.")
