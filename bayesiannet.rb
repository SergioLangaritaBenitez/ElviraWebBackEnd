require 'json'
require_relative  'node'

class BayesianNet


    def  initialize(stringNet)
        @nodes=self.createNet(stringNet)
        @allresult
        
    end

    def createNet(text)
        data = JSON.parse(text)
        nodes=Array.new(data['nodes'].length())
        for i in (0 .. data['nodes'].length()-1) do 
            actualnode=data['nodes'][i]
            #puts"#{actualnode["matrix"]}"
            nodes[i]=Node.new(actualnode["id"],actualnode["state"],actualnode["matrix"],nil) 
            #puts"#{nodes[i]}"
        end
        for conne in data['connections'] do
            for i in (0 .. nodes.length()-1) do
                if nodes[i].getId == conne["to"] then
                    nodeto=nodes[i]
                elsif nodes[i].getId == conne["from"] then
                    nodefrom=nodes[i]
                end
            end
            nodeto.addParent(nodefrom)
        end
        return nodes
    end
    
    def evaluateNet
        thelastnode=getlastNode
        thelastnode.oneStepCalculate()
        #@nodes[0].calculateMatrix()
        #@nodes[0].crearVector(values)
        #puts"#{@nodes[0].getFinalResult}"
        #puts"#{@nodes[0].getFinalmatrix}"
        #@nodes[0].sumFile()
        #puts"#{@nodes[0].getFinalResult}"
        #puts"#{@nodes[0].getFinalmatrix}"
        #return @finalResult
        #puts "#{@nodes}"
        return thelastnode.getFinalResult
    end

    def getResult
        result={}
        for node in @nodes do
            #puts "#{node}"
            result=node
        end
        return result
    end

    def getlastNode
        puts "#{@nodes}"
        its=[]
        
        for i in (0 .. @nodes.length()-1) do
            its.push(@nodes[i])
            if @nodes[i].getParents != nil then
                for parent in @nodes[i].getParents do
                    its.pop(parent)
                end
            end
            #puts "#{}"
        end
        puts "estos nodos #{its} ---------"
        return its[0]
    end

end
