import os
import json
import shutil
from pathlib import Path

try:
    from generate_indexes import generate_indexes
except ImportError:
    generate_indexes = None

def move_non_remaster_files():
    # Define paths
    script_dir = Path(__file__).parent.absolute()
    base_dir = (script_dir / ".." / "sharedUI" / "src" / "commonMain" / "composeResources" / "files").resolve()
    old_dir = (script_dir / "old").resolve()

    if not base_dir.exists():
        print(f"Error: Base directory '{base_dir}' does not exist.")
        return

    print(f"Scanning for non-remaster files in: {base_dir}")
    print(f"Moving to: {old_dir}")

    count = 0
    for root, dirs, files in os.walk(base_dir):
        for file_name in files:
            if not file_name.endswith('.json'):
                continue

            # Skip architectural files
            if file_name in ['index.json', '_folders.json']:
                continue

            file_path = Path(root) / file_name
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    data = json.load(f)

                # Check if it's a data file (has 'system' key)
                if 'system' in data:
                    publication = data.get('system', {}).get('publication', {})
                    is_remaster = publication.get('remaster', False)

                    if not is_remaster:
                        # Calculate relative path to maintain structure in scripts/old
                        rel_path = file_path.relative_to(base_dir)
                        dest_path = old_dir / rel_path

                        # Create destination directory if it doesn't exist
                        dest_path.parent.mkdir(parents=True, exist_ok=True)

                        # Move the file, overwrite if exists
                        if dest_path.exists():
                            os.remove(dest_path)
                        shutil.move(str(file_path), str(dest_path))
                        print(f"Moved: {rel_path}")
                        count += 1
            except Exception as e:
                print(f"Error processing {file_path}: {e}")

    print(f"Total files moved: {count}")

    if count > 0 and generate_indexes:
        print("Updating index files...")
        generate_indexes(str(base_dir))

if __name__ == "__main__":
    move_non_remaster_files()
