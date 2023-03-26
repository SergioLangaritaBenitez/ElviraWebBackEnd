
class Node


    def  initialize(id,states,matrix,parents)
        @id=id
        @states=states
        @matrix=matrix
        @parents=parents
        @finalmatrix=Array.new(matrix.length()){Array.new(matrix[0].length())}
        @finalResult=[]
    end
    def getId
        return @id
    end
    def getStates
        return @states
    end
    def getNumState
        return @states.length()
    end

    def addParent(newParent)
        if @parents == nil
            @parents=[newParent]
        else
            @parents.push(newParent)
        end
    end

    def getParents
        return @parents
    end
    
    def getParentsStates
        result=[]
        if @parents != nil then
            for father in @parents do
                result.push(father.getStates)
            end
        end
        return result
    end

    def getParentsProb
        result=[]
        if @parents != nil then
            for father in @parents do
                father.oneStepCalculate
                result.push(father.getFinalmatrix)
            end

        end
        return result
    end

    def numOfInput
        if @parents==nil then
            return 0
        else
            parents_states=self.getParentsStates
            acumulate=1
            for one_parent in parents_states do
                acumulate=acumulate*one_parent.length() 
            end
            return acumulate
        end
    end
    
    def calculateWeight(all_inputs)
        if all_inputs.length() == 1 then
            return all_inputs[0]
        else 
            return crearVector(all_inputs)
        end
    end


    def calculateMatrix()
        #Comprobar que vector_input.lenght() == numOfInput matrix columnas
        if @parents == nil then
            @finalmatrix=@matrix[0]
            @finalResult=@matrix[0]
        else
            vector_input=self.getParentsProb
            height=self.calculateWeight(vector_input)
            for index in (0 .. height.length()-1)do
                for linea in (0.. @matrix.length()-1) do
                    @finalmatrix[linea][index]= height[index] * @matrix[linea][index]
                end
            end
        end 

    end

    def getFinalmatrix()
        return @finalmatrix
    end

    def sumFile()
        #self.calculateMatrix
        if  @parents != nil then
            for linea in (0.. @finalmatrix.length()-1) do
                actual=0
                for column in (0.. @finalmatrix[linea].length()-1) do
                    actual+=@finalmatrix[linea][column]
                end
                @finalResult[linea]=actual
            end
        end

    end

    def getFinalResult
        return @finalResult
    end
    
    def oneStepCalculate()
        self.calculateMatrix()
        self.sumFile()
        return @finalResult
    end
    """
    double[] vector = new double[total];
        for(int i=0; i< vector.length; i++){
            vector[i]=1;
        }

        int sumatorio=1;
        int lugar=0;
        for(int i=indices.size()-1;i>=0;i--){
            for(int vec=0; vec<total;vec++){
                for(int k=0; k< sumatorio;k++){          
                    vector[vec+k]*=indices.elementAt(i)[lugar];
                }
                lugar++;
                if(lugar==indices.elementAt(i).length)lugar=0;
                
                vec+=sumatorio-1;
            }
            lugar=0;
            
            sumatorio=sumatorio*indices.elementAt(i).length;
        }
        
        
        return vector;
    """
    def crearVector(all_inputs)
        #inicializar vector a 1 longuitud numOfInput
        newVector=Array.new(numOfInput)
        for v in (0..newVector.length()-1) do
            newVector[v]=1
        end
        sumatorio=1
        lugar=0
        for i in (0 .. all_inputs.length()-1) do 
            vec=0
            while vec <= (newVector.length()-1)
                k=0
                while k < sumatorio
                    newVector[vec+k]*=all_inputs[i][lugar];
                    k+=1
                end
                lugar+=1
                if lugar==all_inputs[i].length() then
                    lugar=0
                end
                vec+=sumatorio-1;
                vec+=1
            end
            lugar=0;
            sumatorio*=all_inputs[i].length();
        end
        return newVector
    end
end
