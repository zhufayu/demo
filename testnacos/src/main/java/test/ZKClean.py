import sys
import getopt
from kazoo.client import KazooClient

def main():
    name = None
    zk = None
    count = None

    argv = sys.argv[1:]

    try:
        opts, args = getopt.getopt(argv, "n:z:c:", ["name=", "zk=", "count="])

    except:
        print("Error")

    for opt, arg in opts:
        if opt in ['-n', '--name']:
            name = arg
        elif opt in ['-z', '--zk']:
            zk = arg
        elif opt in ['-c', '--count']:
            count = arg

    print( "name:" + name + " zk:" + zk + " count:" + count)

    zkClient = KazooClient(hosts=zk)
    zkClient.start()

    for num in range(0,round(float(count))):
        path = "/dubbo/" + name + str(num)
        print (path)
        try:
            zkClient.delete(path,recursive=True)
        except Exception as inst:
            print("Unexpected error: %s"%inst)

    zkClient.stop()

#python3 cleanzk.py -n test.service.gen.DemoService -z 10.248.224.74:2181,10.248.224.66:2181,10.248.224.72:2181 -c 10
#python3 cleanzk.py -n test.service.gen.SamplingTaskRequestDemoService -z 10.248.224.74:2181,10.248.224.66:2181,10.248.224.72:2181 -c 5
if __name__ == '__main__':
    main()

