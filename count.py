import glob
count = 0
for filename in glob.iglob('src/com/extensys/vault/**/*.java', recursive=True):
    count+=len(open(filename,"r").readlines())
print(count)
open("lines.txt","w").write(str(count))