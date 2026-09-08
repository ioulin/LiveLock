import zipfile, io, os, re
BASE = '/data/user/0/com.orailnoor.droiddesk/files/home/make4'
aar = os.path.join(BASE, 'filament-android.aar')
z = zipfile.ZipFile(aar)
cj = z.read('classes.jar')
out = open(BASE + '/fieldtypes.log', 'w')
j = zipfile.ZipFile(io.BytesIO(cj))

# 클래스 내 필드/메서드 descriptor를 직접 파싱 (참조 타입명 추출)
for full in ['com/google/android/filament/Renderer$ClearOptions.class', 'com/google/android/filament/Camera.class']:
    data = j.read(full)
    # 필드 시그니처 (Lcom/google/...;) 추출
    refs = re.findall(rb'Lcom/google/android/filament/math/[A-Za-z0-9_]+;', data)
    refs = [r.decode() for r in refs]
    uq = []
    for r in refs:
        if r not in uq:
            uq.append(r)
    out.write('=== %s math refs ===\n' % full)
    out.write('\n'.join(uq) + '\n')
    # setProjectionFov 관련 주변 시그니처
    idx = data.find(b'nSetProjectionFov')
    if idx >= 0:
        out.write('\n--- nSetProjectionFov context ---\n')
        out.write(data[max(0,idx-60):idx+80].decode('latin1') + '\n')
out.close()
print('OK5')
