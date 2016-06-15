import rhinoscriptsyntax as rs
import Rhino as rh

def Import(filename):
    file=open(filename,"r")
    lines=file.readlines()
    
    for i in range(1,len(lines)):
        values=lines[i].split(",")
        if(float(values[3])==1):
            rs.AddPoint(float(values[0]),-float(values[1]),float(values[2]))

rs.EnableRedraw(False)
Import("rhinocsv.csv")