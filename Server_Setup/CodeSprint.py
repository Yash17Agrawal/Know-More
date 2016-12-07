from bs4 import BeautifulSoup
import requests
import re
from flask import Flask, redirect, url_for, request
from googleapiclient.discovery import build


app = Flask(__name__)


def hello_world():
   return "hello"

def gfg(url):
    print("hello")
    source_code = requests.get(str(url))
    plain_text = source_code.text
    soup = BeautifulSoup(plain_text, "html.parser")

    for i in soup.findAll('div',{'class':'entry-content'}):
        var=i.contents

    article= re.sub('<.*?>', '',str(var))
    return (article.replace('\\n', '\n'))

my_api_key = "AIzaSyC1gkYHQMTPdbBYtYPHMk45rkdr4grKq1g"
my_cse_id = "009652909963488507983:kcai5g0i2zg"


def google_search(search_term, api_key, cse_id, **kwargs):
    print("hello")
    service = build("customsearch", "v1", developerKey="AIzaSyC1gkYHQMTPdbBYtYPHMk45rkdr4grKq1g")
    res = service.cse().list(q=search_term, cx=cse_id, **kwargs).execute()
    return res['items']


def give_me_the_url(var1):
    query= var1+" geeksforgeeks"
    results = google_search(query , my_api_key, my_cse_id, num=1)
    for i in results:
        var=i['link']
    return var


def find_tags(url):
    source_code = requests.get(str(url))
    plain_text = source_code.text
    soup = BeautifulSoup(plain_text, "html.parser")

    for i in soup.findAll('div',{'class':'categoryButton'}):
        for k in i.findAll('a',{'rel':'category tag'}):
            var=k.contents[0]

    my_str =var+"$"

    for i in soup.findAll('div', {'class': 'tagButton'}):
        for k in i.findAll('a', {'rel': 'tag'}):
            var = k.contents[0]
    my_str=my_str+var

    return (my_str)

@app.route('/article',methods = ['POST', 'GET'])
def article():
    if request.method == 'POST':
        print("reached server")
        ppap = request.data
        data = ppap[10:-2]
        # print("Enter query")
        # query=raw_input()

        url=give_me_the_url(str(data))
        final_article=gfg(url)
        tags=find_tags(url)

        final_matter=str(final_article)+"$"+str(tags)
        return (final_matter)
    else:
        url = give_me_the_url("dfs")
        final_article = gfg(url)
        tags = find_tags(url)

        final_matter = str(final_article) + "$" + str(tags)
        return (final_matter)


@app.route('/video',methods = ['POST', 'GET'])
def video():
    if request.method == 'POST':
        print("reached server")
        ppap = request.data
        data = ppap[10:-2]

        var =  "www.youtube.com/watch "+data
        url = give_me_the_url(str(var))
        return url



if __name__ == '__main__':
   app.run(host="0.0.0.0")