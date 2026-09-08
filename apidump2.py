import urllib.request, zipfile, io, os, re
BASE = '/data/user/0/com.orailnoor.droiddesk/files/home/make4'
aar = os.path.join(BASE, 'filament-android.aar')
if not os.path.exists(aar):
    urllib.request.urlretrieve('https://repo1.maven.org/maven2/com/google/android/filament/filament-android/1.52.0/filament-android-1.52.0.aar', aar)
z = zipfile.ZipFile(aar)
cj = z.read('classes.jar')
out = open(BASE + '/filament_android_api.log', 'w')
out.write('AAR size: %d\n' % os.path.getsize(aar))
j = zipfile.ZipFile(io.BytesIO(cj))
targets = ['com/google/android/filament/android/UiHelper.class',
           'com/google/android/filament/LightManager.class',
           'com/google/android/filament/IndirectLight.class',
           'com/google/android/filament/Camera.class']
for full in targets:
    if full not in j.namelist():
        out.write('=== %s : NOT FOUND ===\n' % full)
        continue
    data = j.read(full)
    runs = re.findall(rb'[A-Za-z_][A-Za-z0-9_]{4,}', data)
    seen = []
    for r in runs:
        s = r.decode()
        if s not in seen:
            seen.append(s)
    out.write('=== %s ===\n' % full)
    out.write('\n'.join(seen) + '\n')
out.close()
print('OK')
