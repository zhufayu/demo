import sys
import getopt
from kazoo.client import KazooClient

def main():
    name = None
    zk = None
    fromC = None
    toC = None

    argv = sys.argv[1:]

    try:
        opts, args = getopt.getopt(argv, "n:z:f:t:", ["name=", "zk=", "fromC=", "toC="])

    except:
        print("Error")

    for opt, arg in opts:
        if opt in ['-n', '--name']:
            name = arg
        elif opt in ['-z', '--zk']:
            zk = arg
        elif opt in ['-f', '--fromC']:
            fromC = arg
        elif opt in ['-t', '--to']:
            toC = arg

    print( "name:" + name + " zk:" + zk + " fromC:" + fromC + " toC:" + toC)

    zkClient = KazooClient(hosts=zk)
    zkClient.start()

    for num in range(int(fromC),int(toC)):
        path = "/dubbo/metadata/" + name + str(num)
        print (path)
        try:
            zkClient.delete(path,recursive=True)
        except Exception as inst:
            print("Unexpected error: %s"%inst)

    zkClient.stop()
#python3 cleanmetadata.py -n test.service.gen.SamplingTaskRequestDemoService -z 10.16.245.21:2181,10.16.245.37:2181,10.16.245.19:2181 -f 4000 -t 8000
if __name__ == '__main__':
    main()