import sys
import os
import csv
import nltk
from nltk.tokenize import sent_tokenize, word_tokenize

#Making current path in operating system
os.chdir('/Users/vishalnayanshi/rumorvenv/RumorAnalytics/elasticconnector-Advanced')

def Opinions(tweets):
    sentense = word_tokenize(tweets)
    word_features = []

    for i,j in nltk.pos_tag(sentense):
        if j in ['JJ', 'JJR', 'JJS', 'RB', 'RBR', 'RBS']: 
            word_features.append(i)

    Rating = 0
    PositiveWords = 0
    Negativewords = 0

    for i in word_features:
        with open('NegativePositiveWords.txt', 'rU') as f:
            reader = csv.reader(f, delimiter=',')
            for row in reader:
                if i == row[0]:
                    # print i, row[1]
                    if row[1] == 'pos':
                        Rating = Rating + 1
                        PositiveWords = PositiveWords + 1
                    elif row[1] == 'neg':
                        Rating = Rating - 1                  
    return Rating

# # For Testing Pusposes
# print Opinions('What can I say about this place. The staff of the restaurant is nice and the eggplant is not bad. Apart from that, very uninspired food, lack of atmosphere and too expensive. I am a staunch vegetarian and was sorely dissapointed with the veggie options on the menu. Will be the last time I visit, I recommend others to avoid')