import os
import json
import sys

def generate_indexes(base_dir):
    """
    Traverses the directory tree from base_dir and creates an index.json
    in each folder, containing a list of subdirectories and files.
    """
    if not os.path.exists(base_dir):
        print(f"Error: Directory '{base_dir}' does not exist.")
        return

    for root, dirs, files in os.walk(base_dir):
        # We might want to exclude existing index.json files
        # so they don't list themselves
        valid_files = [
            f for f in files
            if f != 'index.json' and f != 'index.txt' and not f.endswith('.zip')
        ]

        # We can also sort them for consistency
        valid_files.sort()
        dirs.sort()

        file_entries = []
        for file_name in valid_files:
            if file_name.endswith('.json') and file_name != '_folders.json':
                file_path = os.path.join(root, file_name)
                try:
                    with open(file_path, 'r', encoding='utf-8') as json_file:
                        data = json.load(json_file)

                        # Extract useful info
                        entry = {
                            "fileName": file_name,
                            "id": data.get("_id"),
                            "name": data.get("name"),
                            "img": data.get("img"),
                            "type": data.get("type", None)
                        }

                        system = data.get("system", {})
                        if "level" in system:
                            level = system["level"]
                            if isinstance(level, dict) and "value" in level:
                                entry["level"] = level["value"]
                            elif isinstance(level, int):
                                entry["level"] = level

                        if "category" in system:
                            entry["category"] = system.get("category")

                        # Clean up None values to keep index compact
                        entry = {k: v for k, v in entry.items() if v is not None}
                        file_entries.append(entry)
                except Exception as e:
                    print(f"Error reading {file_path}: {e}")
                    # Fallback for non-JSON or broken JSON
                    file_entries.append({"fileName": file_name})
            else:
                file_entries.append({"fileName": file_name})

        index_data = {
            "directories": dirs,
            "files": file_entries
        }

        index_path = os.path.join(root, 'index.json')
        with open(index_path, 'w', encoding='utf-8') as f:
            json.dump(index_data, f, ensure_ascii=False, indent=2)

        print(f"Generated {index_path}")

if __name__ == "__main__":
    # Point to the files directory inside composeResources
    # Using relative path from the scripts folder to the actual files directory
    script_dir = os.path.dirname(os.path.abspath(__file__))
    default_target_dir = os.path.join(script_dir, "..", "sharedUI", "src", "commonMain", "composeResources", "files")

    target_dir = sys.argv[1] if len(sys.argv) > 1 else default_target_dir
    target_dir_abs = os.path.abspath(target_dir)

    print(f"Starting to generate index files in: {target_dir_abs}")
    generate_indexes(target_dir_abs)
    print("Done!")
