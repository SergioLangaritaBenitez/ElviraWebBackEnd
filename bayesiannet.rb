require 'json'
require_relative  'node'

class BayesianNet


    def  initialize(stringNet)
        @nodes=self.createNet(stringNet)
        @allresult
        
    end

    def createNet(text)
        data = JSON.parse(text)
        evaluate=data["evaluate"]
        nodes=Array.new(data['nodes'].length())
        for i in (0 .. data['nodes'].length()-1) do 
            actualnode=data['nodes'][i]
            #puts"#{actualnode["matrix"]}"
            nodes[i]=Node.new(actualnode["id"],actualnode["state"],actualnode["matrix"],nil) 
            if actualnode["id"] == evaluate then
                @lastnode=nodes[i]
            end
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
        @lastnode.oneStepCalculate()
        return @lastnode.getFinalResult
    end

    def getResult
        result='{"result":['
        for node in @nodes do
            result+='{"id":"'+node.getId.to_s+'","state":'+node.getStates.to_s+',"matrix":'+node.getFinalResult.to_s+'},'
        end
        result=result[0..-2]+"]}"
        return result
    end



end
