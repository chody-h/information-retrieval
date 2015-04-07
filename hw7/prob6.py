
from __future__ import division
from collections import OrderedDict
from math import pow
from math import sqrt
# from math import abs

files = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"]
doc_vectors = {}
vocab = OrderedDict()
groups = {"01": {"01": None}, "04": {"04": None}, "07": {"07": None}}
centroids = {}

def ComputeCosineSimilarity(c, d):
	v1 = doc_vectors[d]
	v2 = centroids[c]

	dotprod = 0
	v1len = 0
	v2len = 0
	for word in v1:
		dotprod += v1[word] * v2[word]
		v1len += pow(v1[word], 2)
		v2len += pow(v2[word], 2)
	return dotprod/(sqrt(v1len * v2len))

# returns whether or not the centroids changed
def RecomputeCentroids():
	changed = False
	for groupname in groups:
		centroid_vector = centroids[groupname]
		copy = dict(centroid_vector)
		for word in centroid_vector:
			avg_of_word = 0
			for doc_name in groups[groupname]:
				avg_of_word += doc_vectors[doc_name][word]
			avg = avg_of_word / float(len(groups[groupname]))
			centroid_vector[word] = avg
		for word in centroid_vector:
			if abs(copy[word] - centroid_vector[word]) > 0.01:
				changed = True
				break
	return changed

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

# initialize centroids
for groupnames in groups:
	centroids[groupnames] = dict(doc_vectors[groupnames])

# compute cosine similarity to categorize each file for the first time
for fname in files:
	closestgroup = (float("inf"), None)
	if fname != "01" and fname != "04" and fname != "07":
		for groupname in groups:
			similarity = ComputeCosineSimilarity(d=fname, c=groupname)
			dist = 1 - similarity
			if dist < closestgroup[0]:
				closestgroup = (dist, groupname)
			# print fname, dist, groupname
		groups[closestgroup[1]][fname] = None

print "Starting groups"
for group in groups:
	print group, groups[group]

count = 0
while RecomputeCentroids():
	count = count + 1
	print "\nIteration", count

	# compute cosine similarity to categorize each file
	for fname in files:
		closestgroup = (float("inf"), None)
		if fname != "01" and fname != "04" and fname != "07":
			for groupname in groups:
				similarity = ComputeCosineSimilarity(d=fname, c=groupname)
				dist = 1-similarity
				if dist < closestgroup[0]:
					closestgroup = (dist, groupname)
				# print fname, dist, groupname
			for groupname in groups:
				try:
					del groups[groupname][fname]
				except KeyError:
					pass
			groups[closestgroup[1]][fname] = None

	for group in groups:
		print group, groups[group]

# for doc_vector in doc_vectors:
# 	print doc_vector, doc_vectors[doc_vector]

# for word in vocab:
# 	print word