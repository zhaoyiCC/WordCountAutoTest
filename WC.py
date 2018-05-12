import collections
import re
import string
import sys, getopt
import os

                
#with open('rural.txt') as f:
#	for line in f:
#		print(line)
cnt_character = 0
cnt_word = 0
cnt_line = 0

def countAsciiChars(str):
        tot = 0
        for ch in str:
                if ch in string.ascii_letters or ch == ' ' or ch == '\t' or ch=='\n':
                    print(ch,'MMMMMM')
                    tot = tot+1
        return tot

def fileExtension(path):
        return os.path.splitext(path)[1]

def workFile(filePath, m): #-r -m
        f = open(filePath, 'r', encoding = 'utf-8')
        texts = f.read()
        #print(texts) 
	
        words = re.split(r"[^a-zA-Z0-9]", texts)#texts.split()
        wordsFilter = []
        for i in words:
                if (len(i) >= 4 and i[0:4].isalpha()):
                        wordsFilter.append(i.lower())
        #print("------",str(wordsFilter))
        #print('characters: '+ str(countAsciiChars(texts)))
        #print('words: '+ str(len(words)))
        #print('lines: '+ str(texts.count('\n')+1))
        global cnt_character
        global cnt_word
        global cnt_line                       
        cnt_character = cnt_character + countAsciiChars(texts)
        cnt_word = cnt_word + len(wordsFilter)
        #print(texts)
        #print(texts.count('\n'))
        #print("--------------")
        cnt_line = cnt_line + texts.count('\n') - 1
        #wordCol = collections.Counter(wordsFilter)
        #freq = wordCol.most_common(10) #collect most 10 words
        #print(freq)
        
        phrases = []
        if (m == 0): # -n collect phrases
                return wordsFilter, phrases
        wordsCnt = len(wordsFilter)
        for i in range(0, wordsCnt - m +1):
                isPhrase = 1
                nowPhrase = ""
                for j in range(i, i+m):
                        nowWord = words[j]
                        if not (len(nowWord) > 4 and nowWord[0:4].isalpha()):
                                isPhrase = 0
                                break
                        nowPhrase += " "+nowWord
                if isPhrase:
                        phrases.append(nowPhrase[1:len(nowPhrase)]) #remove the first ' '
        #phraseCol = collections
        #print(phrase)
        return wordsFilter, phrases


opts, args = getopt.getopt(sys.argv[1:], "r:m:n:")
print(opts)
print(args)
input_file=""
output_file=""
filePath = "./tests"
n = 10 #default n
m = 0 #default m
for op, value in opts:
    if op == "-r":
        filePath = value
        print(op+":  +"+value)
    elif op == "-m":
        print(op+":  +"+value)
        m = int(value)
    elif op == "-n":
        print(op+":  +"+value)
        n = int(value)
        

wordsAll = []
phrasesAll = []
for fpathe,dirs,fs in os.walk(filePath):#./tests'):
        for f in fs:
                filePath = os.path.join(fpathe,f)
                if (fileExtension(filePath) != ".txt"):
                        continue
                print(filePath)
                wordsNow, phrasesNow = workFile(filePath, m)
                print(wordsNow)
                wordsAll = wordsAll + wordsNow
                phrasesAll = phrasesAll + phrasesNow

fileOut = open("result.txt", "w")
print('characters: '+ str(cnt_character))
print('words: '+ str(cnt_word))
print('lines: '+ str(cnt_line))
fileOut.write('characters: '+ str(cnt_character))
fileOut.write('words: '+ str(cnt_word))
fileOut.write('words: '+ str(cnt_word))

wordsCol = collections.Counter(wordsAll)
wordFreq = wordsCol.most_common(10) #collect most 10 words
phrasesCol = collections.Counter(phrasesAll)
phrasesFreq = phrasesCol.most_common(3)

for i in wordFreq:
        print(i[0]+': '+str(i[1]))
        fileOut.write(i[0]+': '+str(i[1]))

#for i in phraseAll:
        #print(i[0]]+': '+str(i[1])
print(phrasesFreq)
