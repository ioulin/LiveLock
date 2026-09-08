import json, urllib.request, os

BASE = '/data/user/0/com.orailnoor.droiddesk/files/home/make4'
lines = open(BASE + '/ntfy.json').read().strip().splitlines()
url = None
title = None
count = 0
for ln in lines:
    try:
        e = json.loads(ln)
    except Exception:
        continue
    if e.get('event') != 'message':
        continue
    count += 1
    title = e.get('title')
    att = e.get('attachment') or {}
    if att.get('url'):
        url = att['url']
if url:
    try:
        urllib.request.urlretrieve(url, BASE + '/build_log.txt')
        print('LOG_DOWNLOADED', os.path.getsize(BASE + '/build_log.txt'), 'bytes | title:', title)
    except Exception as ex:
        print('DOWNLOAD_FAIL', ex, url)
else:
    print('NO_ATTACHMENT messages:', count)
