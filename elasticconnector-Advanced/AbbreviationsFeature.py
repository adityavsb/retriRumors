import sys
import os
import csv
import nltk
from nltk.tokenize import sent_tokenize, word_tokenize

#Making current path in operating system
os.chdir('/Users/vishalnayanshi/rumorvenv/RumorAnalytics/elasticconnector-Advanced')

def Abbreviationschecker(para):
    sentense = word_tokenize(para)
    word_features = []

    for i,j in nltk.pos_tag(sentense):
        if j in ['JJ', 'JJR', 'JJS', 'RB', 'RBR', 'RBS']: 
            word_features.append(i)

    Rating = " "

    for i in word_features:
        with open('Abbreviations.txt', 'rU') as f:
            reader = csv.reader(f, delimiter=',')
            for row in reader:
                # print row
                if i == row[0]:
                    print i, row[1]
                    if row[1] == 'pos':
                        Rating = 'positive'
                    elif row[1] == 'neg':
                        Rating = 'negative'                  
    return Rating

# #For Testing Purposes
# print Abbreviations('Hello WTF this')