import urllib.request, zipfile, io, os, re
BASE = '/data/user/0/com.orailnoor.droiddesk/files/home/make4'
aar = os.path.join(BASE, 'gltfio.aar')
if not os.path.exists(aar):
    urllib.request.urlretrieve('https://repo1.maven.org/maven2/com/google/android/filament/gltfio-android/1.52.0/gltfio-android-1.52.0.aar', aar)
z = zipfile.ZipFile(aar)
cj = z.read('classes.jar')
out = open(BASE + '/filament_api.log', 'w')
out.write('AAR size: %d\n' % os.path.getsize(aar))
j = zipfile.ZipFile(io.BytesIO(cj))
names = [n for n in j.namelist() if n.startswith('com/google/android/filament/gltfio/') and n.endswith('.class')]
out.write('classes: %d\n' % len(names))
targets = ['FilamentAsset.class', 'Animator.class', 'FilamentInstance.class', 'AssetLoader.class', 'ResourceLoader.class']
for t in targets:
    full = 'com/google/android/filament/gltfio/' + t
    if full not in j.namelist():
        out.write('=== %s : NOT FOUND ===\n' % t)
        continue
    data = j.read(full)
    runs = re.findall(rb'[A-Za-z_][A-Za-z0-9_]{4,}', data)
    seen = []
    for r in runs:
        s = r.decode()
        if s not in seen:
            seen.append(s)
    out.write('=== %s ===\n' % t)
    out.write('\n'.join(seen) + '\n')
out.close()
print('OK')
