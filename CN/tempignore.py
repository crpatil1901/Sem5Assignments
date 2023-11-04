x = [2**i for i in range(8)]
y = [[x[i],x[i+1]] for i in range(len(x)-1)] 





print(y)
for _ in range(int(input())):
    n = int(input()) 
    a = list(map(int,input().split())) 
    ok = True
    for i in y:
        start,end = i[0],min(i[1],n+1) 
        for j in range(start,end-1):
            print(j,j+1)
            if a[j] > a[j+1]:
                ok = False 
                break 
        if not ok: break 
        if end == n+1 : break 
    print("Yes" if ok else "No") 


   


        