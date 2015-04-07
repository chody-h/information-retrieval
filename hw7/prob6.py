from collections import OrderedDict
from math import pow
from math import sqrt

def ComputeCosineSimilarity(doc1, doc2):
	v1 = doc_vectors[doc1]
	v2 = doc_vectors[doc2]

	dotprod = 0
	v1len = 0
	v2len = 0
	for word in v1:
		dotprod += v1[word] * v2[word]
		v1len += pow(v1[word], 2)
		v2len += pow(v2[word], 2)
	return dotprod/(sqrt(v1len * v2len))

files = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"]
doc_vectors = {}
vocab = OrderedDict()
groups = {"01": {"01": None}, "04": {"04": None}, "07": {"07": None}}

# load vocabulary
for fname in files:
	with open(fname) as f:
		doc_vectors[fname] = f.read()
		f.seek(0)
		for word in f:
			vocab[word.rstrip()] = None

# load document vectors
for fname in files:
	doc_vector = {}
	for word in vocab:
		if word in doc_vectors[fname]:
			doc_vector[word] = 1
		else:
			doc_vector[word] = 0
	doc_vectors[fname] = doc_vector

# compute cosine similarity to categorize each file
for fname in files:
	print "fname: ", fname
	closestgroup = (float("inf"), None)
	if fname != "01" and fname != "04" and fname != "07":
		for groupname in groups:
			print "groupname: ", groupname
			setobjects = groups[groupname]
			print "setobjects: ", setobjects
			maxdist = (0, None)
			for docname in setobjects:
				print "docname: ", docname
				similarity = ComputeCosineSimilarity(fname, docname)
				print "similarity: ", similarity
				dist = 1 - similarity
				print "dist: ", dist
				if dist > maxdist[0]:
					maxdist = (dist, docname)
					print "maxdist: ", maxdist
			if maxdist[0] < closestgroup[0]:
				closestgroup = (maxdist[0], groupname)
				print "closestgroup: ", closestgroup
		print "fname, closestgroup: ", fname, closestgroup
		break


# for doc_vector in doc_vectors:
# 	print doc_vector, doc_vectors[doc_vector]

# for word in vocab:
# 	print word