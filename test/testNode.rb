ENV['APP_ENV'] = 'test'

require 'test/unit'
require 'rack/test'
require_relative  '../node'

class NodeTest < Test::Unit::TestCase
  include Rack::Test::Methods

  def app
    Sinatra::Application
  end

  def id
    a=Node.new("A",nil,nil,nil) 
    b=Node.new("b",nil,nil,nil) 
    n2=Node.new("esternocleidomastoideo",nil,nil,nil) 
    assert_equal "A", a.getId
    assert_equal "b", b.getId
    assert_equal "esternocleidomastoideo", n4.getId
  end

  def test_states
    n2=Node.new(nil,["si","no"],[[1,2,3]],nil) 
    n3=Node.new(nil,["s","m","l"],[[1,2,3]],nil) 
    n4=Node.new(nil,["a","b","c","d"],[[1,2,3]],nil) 
    n5=Node.new(nil,["1","2","3","4","5"],[[1,2,3]],nil) 
    assert_equal 2, n2.getNumState
    assert_equal 3, n3.getNumState
    assert_equal 4, n4.getNumState
    assert_equal 5, n5.getNumState
  end

  def test_getParentsStates
    nothing=Node.new(nil,["si","no"],[[1,2,3]],nil) 
    n1=Node.new(nil,["s","m","l"],[[1,2,3]],[nothing]) 
    assert_equal [], nothing.getParentsStates
    assert_equal [["si","no"]], n1.getParentsStates

    nothing1=Node.new(nil,["arriba","abajo"],[[1,2,3]],nil) 
    n1=Node.new(nil,["s","m","l"],[[1,2,3]],[nothing,nothing1]) 
    assert_equal [], nothing1.getParentsStates
    assert_equal [["si","no"],["arriba","abajo"]], n1.getParentsStates
  end

  def test_Input
    nothing=Node.new(nil,["si","no"],[[1,2,3]],nil) 
    n3=Node.new(nil,["s","m","l"],[[1,2,3]],[nothing])   
    assert_equal 0, nothing.numOfInput
    assert_equal 2, n3.numOfInput

    nothing1=Node.new(nil,["arriba","abajo","helloworld"],[[1,2,3]],nil) 
    n1=Node.new(nil,["s","m","l"],[[1,2,3]],[nothing,nothing1]) 
    n2=Node.new(nil,["s","m","l"],[[1,2,3]],[n3,n1,nothing1])
    assert_equal 0, nothing1.numOfInput
    assert_equal 6, n1.numOfInput
    assert_equal 27, n2.numOfInput
  end
  def test_matrix
    n=Node.new(nil,["s","m","l"],[[2,3,4]],nil) 
    n1=Node.new(nil,["sx","m","l"],[[1,2,3],[4,5,6],[7,8,9]],[n])
    n.calculateMatrix
    puts"#{n.getFinalResult}"
    n1.calculateMatrix
    n1.sumFile
    assert_equal [2,3,4], n.getFinalmatrix()

    assert_equal [[2,6, 12], [8, 15, 24], [14, 24, 36]], n1.getFinalmatrix()
    assert_equal [20, 47, 74], n1.getFinalResult
    n2=Node.new(nil,["s","m","l"],[[2,3,4,5]],nil) 
    n1=Node.new(nil,["s","m","l"],[[1,2,3,4],[4,5,6,3],[7,8,9,2],[1,2,3,1]],[n2]) 
    n1.calculateMatrix
    n1.sumFile 
    assert_equal [[2, 6, 12,20], [8, 15, 24,15], [14, 24, 36,10],[2,6,12,5]], n1.getFinalmatrix()
    assert_equal [40, 62, 84,25], n1.getFinalResult()
    
    n2=Node.new(nil,["s","m","l","a"],[[2,3,4,5]],nil) 
    n1=Node.new(nil,["s","m","l","1"],[[1,2,3,4],[4,5,6,3],[7,8,9,2]],[n2]) 
    weight = n1.oneStepCalculate() 
    assert_equal [40, 62, 84],weight
  end

  def test_vector
    n1=Node.new(nil,["s","m","l"],[[1,2,3]],nil) 
    n3=Node.new(nil,["s","m","l"],[[1,2,3]],nil) 
    n2=Node.new(nil,["s","m","l"],[[1,2,3],[4,5,6],[7,8,9]],[n1,n3])
    values=[[1,2,3],[1,2,3]]
    asser=n2.crearVector(values)
    assert_equal [1, 2, 3, 2, 4, 6, 3, 6,9],asser
  end
  
  def test_matrix1
    n1=Node.new(nil,["s","m","l"],[[0.6,0.1,0.3]],nil) 
    n2=Node.new(nil,["s","m","l"],[[0.25,0.3,0.1],[0.15,0.7,0.1],[0.6,0,0.8]],[n1])
    n2.calculateMatrix
    puts "#{n2.getFinalmatrix}"
    n2.sumFile
    puts "#{n2.getFinalResult}"
    
    #assert_equal [1, 2, 3, 2, 4, 6, 3, 6,9],n2.getFinalResult
    assert_equal 1,n2.getFinalResult.sum

  end

  def test_addParent
    n1=Node.new("a",["s","m","l"],[[0.6,0.1,0.3]],nil) 
    n2=Node.new("b",["s","m","l"],[[0.25,0.3,0.1],[0.15,0.7,0.1],[0.6,0,0.8]],nil)
    n3=Node.new("c",["s","m","l"],[[0.25,0.3,0.1],[0.15,0.7,0.1],[0.6,0,0.8]],nil)
    n2.addParent(n1)
    assert_equal "a",n2.getParents[0].getId
    n2.addParent(n3)
    assert_equal "c",n2.getParents[1].getId
    n4=Node.new("d",["s","m","l"],[[0.25,0.3,0.1],[0.15,0.7,0.1],[0.6,0,0.8]],nil)
    n4.addParent(n2)
    assert_equal "b",n4.getParents[0].getId

    assert_equal "a",n4.getParents[0].getParents[0].getId
    assert_equal "c",n4.getParents[0].getParents[1].getId

  end

end