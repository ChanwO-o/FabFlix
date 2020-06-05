print('Starting TJ_TS parse')
filename = 'TJ_TS.txt'
with open(filename, 'r') as file:
	tjList = []
	tsList = []
	for line in file:
		print(line, end = '')
		tjts = line.strip().split(' ')
		tjList.append(int(float(tjts[0])))
		tsList.append(int(float(tjts[1])))
	print('tjList:', tjList)
	print('tsList:', tsList)
	print('TJ:', sum(tjList) / len(tjList))
	print('TS:', sum(tsList) / len(tsList))
file.close()
