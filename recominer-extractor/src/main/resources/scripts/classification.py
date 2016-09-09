porco1 = [1, 1, 0]
porco2 = [1, 1, 0]
porco3 = [1, 1, 0]
cachorro4 = [1, 1, 1]
cachorro5 = [0, 1, 1]
cachorro6 = [0, 1, 1]

dados = [porco1, porco2, porco3, cachorro4, cachorro5, cachorro6]

marcacoes = [1, 1, 1, -1, -1, -1]

from numpy import genfromtxt
train = genfromtxt('/home/kuroda/NetBeansProjects/extractor/generated/avro/259/1281/256/train.csv', skip_header=1, names="file1Id, file1, file2Id, file2, id, issueId, commitId, issueKey, issueType, issuePriority, issueAssignedTo, issueSubmittedBy, commenters, devCommenters, issueAge, wordinessBody, wordinessComments, comments, networkId, networkIssueId, networkCommitId, btwMdn, clsMdn, dgrMdn, efficiencyMdn, efvSizeMdn, constraintMdn, hierarchyMdn, size, ties, density, diameter, commitMetricId, commitMetricCommitId, revision, committer, fileMetricId, fileId, committers, commits, fileAge, addedLines, deletedLines, changedLines, cochanged", delimiter=';', dtype=[('file1Id', '<i4'), ('file1', '|S1024'), ('file2Id', '<i4'), ('file2', '|S1024'), ('id', '<i4'), ('issueId', '<i4'), ('commitId', '<i4'), ('issueKey', '|S32'), ('issueType', '|S32'), ('issuePriority', '|S32'), ('issueAssignedTo', '|S128'), ('issueSubmittedBy', '|S128'), ('commenters', '<i4'), ('devCommenters', '<i4'), ('issueAge', '<i4'), ('wordinessBody', '<i4'), ('wordinessComments', '<i4'), ('comments', '<i4'), ('networkId', '<i4'), ('networkIssueId', '<i4'), ('networkCommitId', '<i4'), ('btwMdn', '<f8'), ('clsMdn', '<f8'), ('dgrMdn', '<f8'), ('efficiencyMdn', '<f8'), ('efvSizeMdn', '<f8'), ('constraintMdn', '<f8'), ('hierarchyMdn', '<f8'), ('size', '<i4'), ('ties', '<i4'), ('density', '<f8'), ('diameter', '<i4'), ('commitMetricId', '<i4'), ('commitMetricCommitId', '<i4'), ('revision', '|S128'), ('committer', '|S128'), ('fileMetricId', '<i4'), ('fileId', '<i4'), ('committers', '<i4'), ('commits', '<i4'), ('fileAge', '<i4'), ('addedLines', '<i4'), ('deletedLines', '<i4'), ('changedLines', '<i4'), ('cochanged', '<i4')])
print(train)

#from sklearn.naive_bayes import MultinomialNB
from sklearn.ensemble import RandomForestClassifier

#modelo = MultinomialNB()
modelo = RandomForestClassifier(n_estimators=10)

modelo.fit(dados, marcacoes)

misterioso1 = [1, 1, 1]
misterioso2 = [1, 0, 0]
misterioso3 = [0, 0, 1]

teste = [misterioso1, misterioso2, misterioso3]

marcacoes_teste = [-1, 1, -1]

resultado = modelo.predict(teste)

print(resultado)
