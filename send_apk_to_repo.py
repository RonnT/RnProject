__author__ = 'Alexander Smirnov'

from config import *
from bs4 import BeautifulSoup
import os.path
import requests
import sys

if len(sys.argv) > 2:
    if os.path.isfile(sys.argv[2]):
        r = requests.post('http://android.sitesoft.ru/auth/login', data=config)
        cookies = dict(laravel_session=r.cookies['laravel_session'])

        url_app = 'http://android.sitesoft.ru/apps/' + sys.argv[1]
        r = requests.get(url_app, cookies=cookies)
        page = BeautifulSoup(r.text, 'html5lib')

        forms = page.find_all('form')
        if len(forms) > 0:
            url_to_send = forms[0].attrs['action']

            if "upload" in url_to_send:
                files = {
                    'file_app': (
                        'app-release.apk',
                        open(sys.argv[2], 'rb'),
                        'application/octet-stream'
                    )
                }

                r = requests.post(url_to_send, data={'change': sys.argv[3]}, files=files, cookies=cookies)
                print(url_app)
            else:
                print('Ошибка URL формы отправки файла')
        else:
            print('Форма не найденна')
    else:
        print('Файл не найден')
else:
    print('Недостаточно агрументов')