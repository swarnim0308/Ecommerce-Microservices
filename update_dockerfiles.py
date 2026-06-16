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

docker_files = glob.glob(os.path.join(root_dir, "**", "Dockerfile"), recursive=True)
for df in docker_files:
    replace_in_file(df, "eclipse-temurin:11-jre-alpine", "eclipse-temurin:17-jre-alpine")

print("Dockerfile update completed.")
