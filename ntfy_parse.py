import json, urllib.request, os

BASE = '/data/user/0/com.orailnoor.droiddesk/files/home/make4'
d = json.load(open(BASE + '/ntfy.json'))
evs = d if isinstance(d, list) else d.get('events', [])
url = None
for e in evs:
    att = e.get('attachment') or {}
    if att.get('url'):
        url = att['url']
        break
if url:
    try:
        urllib.request.urlretrieve(url, BASE + '/build_log.txt')
        print('LOG_DOWNLOADED', os.path.getsize(BASE + '/build_log.txt'), 'bytes from', url)
    except Exception as ex:
        print('DOWNLOAD_FAIL', ex, url)
else:
    print('NO_ATTACHMENT events:', len(evs))
