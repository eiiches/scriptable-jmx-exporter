# coding: utf-8

import urllib.request
import re
import sys
import argparse

def fetch_metrics(url):
    f = urllib.request.urlopen(url)
    content = f.read().decode('utf-8')

    lines = []
    for line in content.split('\n'):
        line = line.strip()
        if not line:
            continue
        if line.startswith('#'):
            continue
        # line = line.replace('_', '')
        if '{' in line:
            line = line[:line.index('}')+1]
        else:
            line = line[:line.index(' ')]
        lines.append(line)

    return set(lines)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='diff_metrics.py http://foo.example.com:9404/metrics http://foo.example.com:9639/metrics kafka')
    parser.add_argument('url1', metavar='URL1', help='e.g. http://foo.example.com:9404/metrics')
    parser.add_argument('url2', metavar='URL2', help='e.g. http://foo.example.com:9639/metrics')
    parser.add_argument('regex', metavar='REGEX', help='e.g. kafka.*')
    args = parser.parse_args()

    regex = re.compile(args.regex)

    m1 = fetch_metrics(args.url1)
    m2 = fetch_metrics(args.url2)
    removed = m1 - m2
    added = m2 - m1

    diff = []
    for metric in removed:
        diff.append((metric, '-'))
    for metric in added:
        diff.append((metric, '+'))

    diff.sort()
    for metric in diff:
        if not regex.match(metric[0]):
            continue
        print('{} {}'.format(metric[1], metric[0]))

