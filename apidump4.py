import zipfile, io, os, re
BASE = '/data/user/0/com.orailnoor.droiddesk/files/home/make4'
aar = os.path.join(BASE, 'filament-android.aar')
z = zipfile.ZipFile(aar)
cj = z.read('classes.jar')
out = open(BASE + '/renderer_api.log', 'w')
j = zipfile.ZipFile(io.BytesIO(cj))
for full in ['com/google/android/filament/Renderer.class', 'com/google/android/filament/Renderer$ClearOptions.class']:
    data = j.read(full)
    runs = re.findall(rb'[A-Za-z_][A-Za-z0-9_]{3,}', data)
    seen = []
    for r in runs:
        s = r.decode()
        if s not in seen:
            seen.append(s)
    out.write('=== %s ===\n' % full)
    out.write('\n'.join(seen) + '\n\n')
out.close()
print('OK3')
