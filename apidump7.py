import zipfile, io, os
BASE = '/data/user/0/com.orailnoor.droiddesk/files/home/make4'
aar = os.path.join(BASE, 'filament-android.aar')
z = zipfile.ZipFile(aar)
cj = z.read('classes.jar')
j = zipfile.ZipFile(io.BytesIO(cj))
out = open(BASE + '/classscan.log', 'w')
names = [n for n in j.namelist() if n.endswith('.class')]
out.write('total classes: %d\n' % len(names))
for n in names:
    if any(k in n.lower() for k in ['float4', 'color', 'linear']):
        out.write(n + '\n')
# ClearOptions의 명시적 시그니처 (Constant pool 문자열 그대로)
data = j.read('com/google/android/filament/Renderer$ClearOptions.class')
# 모든 필드 descriptor-ish 문자열 추출
import re
s = data.decode('latin1')
for pat in ['clearColor', 'clear', 'discard']:
    idx = s.find(pat)
    if idx >= 0:
        out.write('\n[%s] ctx: %r\n' % (pat, s[max(0,idx-10):idx+25]))
out.close()
print('OK6')
