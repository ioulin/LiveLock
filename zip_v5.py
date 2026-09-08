#!/usr/bin/env python3
import os
import zipfile

project_dir = "/data/user/0/com.orailnoor.droiddesk/files/home/make4"
output_zip = "/data/user/0/com.orailnoor.droiddesk/files/home/LiveLock_5.zip"

exclude = {'.git', '__pycache__', '.gradle', 'build', 'LiveLock.zip', 'LiveLock_5.zip'}

with zipfile.ZipFile(output_zip, 'w', zipfile.ZIP_DEFLATED) as zf:
    for root, dirs, files in os.walk(project_dir):
        dirs[:] = [d for d in dirs if d not in exclude]
        for f in files:
            if f in exclude:
                continue
            full = os.path.join(root, f)
            arc = os.path.relpath(full, project_dir)
            zf.write(full, arc)

size = os.path.getsize(output_zip)
print(f"Done! {output_zip} ({size} bytes)")
