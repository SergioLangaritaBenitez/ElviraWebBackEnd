ENV['APP_ENV'] = 'test'

require 'test/unit'
require 'rack/test'
require_relative  '../bayesiannet'
require 'json'
class NodeTest < Test::Unit::TestCase
  include Rack::Test::Methods

  def app
    Sinatra::Application
  end

 

  def test_creation_one_node
    info='{"nodes":[{"id":"a","state":["si","no"],"matrix":[[1,0]] }], "connections":[], "evaluate":"a"}'
    net=BayesianNet.new(info)
    assert_equal [1,0], net.evaluateNet
  end

  def test_creation_two_node
    info='{"nodes":[
      {"id":"a","state":["si","no"],"matrix":[[0.7,0.3]]},
      {"id":"b","state":["a","b"],"matrix":[[0.1,0.25],[0.9,0.75]]}
      ], "connections":[{"from":"a","to":"b"}], "evaluate":"b"}'
    net=BayesianNet.new(info)
    assert_equal  1, net.evaluateNet.sum

  end

  def test_creation_with_parents
    info='{"nodes":[
      {"id":"a","state":["si","no"],"matrix":[[0.7,0.3]]},
      {"id":"b","state":["a","b"],"matrix":[[0.1,0.9]]},
      {"id":"c","state":["d","v"],"matrix":[[0.1,0.25,0,1],[0.9,0.75,1,0]]}
      ], "connections":[{"from":"a","to":"c"},{"from":"b","to":"c"}], "evaluate":"c"}'
    net=BayesianNet.new(info)
    assert_equal  1, net.evaluateNet.sum
  end


  def test_creation_2_nodes
    info='{"nodes":[
      {"id":"a","state":["si","no"],"matrix":[[0.7,0.3]]},
      {"id":"b","state":["a","b"],"matrix":[[0.1,0.25],[0.9,0.75]]},
      {"id":"c","state":["d","v"],"matrix":[[0.1,0.25],[0.9,0.75]]}
      ], "connections":[{"from":"a","to":"b"},{"from":"b","to":"c"}], "evaluate":"c"}'
    net=BayesianNet.new(info)
    
    assert_equal  1, net.evaluateNet.sum
  end




  def test_return_result
    info='{"nodes":[
      {"id":"a","state":["si","no"],"matrix":[[0.7,0.3]]},
      {"id":"b","state":["a","b"],"matrix":[[0.1,0.9]]},
      {"id":"c","state":["d","v"],"matrix":[[0.1,0.25,0,1],[0.9,0.75,1,0]]}
      ], "connections":[{"from":"a","to":"c"},{"from":"b","to":"c"}], "evaluate":"c"}'
    net=BayesianNet.new(info)
    net.evaluateNet
    data = JSON.parse(net.getResult)
    assert_equal  "a",  data["result"][0]["id"]
    assert_equal  "b",  data["result"][1]["id"]
    assert_equal  "c",  data["result"][2]["id"]
    assert_equal  ["si","no"],  data["result"][0]["state"]
    assert_equal  ["a","b"],  data["result"][1]["state"]
    assert_equal  ["d","v"],  data["result"][2]["state"]
    assert_equal  1,  data["result"][0]["matrix"].sum
    assert_equal  1,  data["result"][1]["matrix"].sum
    assert_equal  1,  data["result"][2]["matrix"].sum
  end

  def test_return_result_2
    info='{"nodes":[
      {"id":"a","state":["si","no"],"matrix":[[0.7,0.3]]},
      {"id":"b","state":["a","b"],"matrix":[[0.1,0.25],[0.9,0.75]]},
      {"id":"c","state":["d","v"],"matrix":[[0.1,0.25],[0.9,0.75]]}
      ], "connections":[{"from":"a","to":"b"},{"from":"b","to":"c"}], "evaluate":"c"}'

    net=BayesianNet.new(info)
    net.evaluateNet
    data = JSON.parse(net.getResult)
    assert_equal  "a",  data["result"][0]["id"]
    assert_equal  "b",  data["result"][1]["id"]
    assert_equal  "c",  data["result"][2]["id"]
    assert_equal  ["si","no"],  data["result"][0]["state"]
    assert_equal  ["a","b"],  data["result"][1]["state"]
    assert_equal  ["d","v"],  data["result"][2]["state"]
    assert_equal  1,  data["result"][0]["matrix"].sum
    assert_equal  1,  data["result"][1]["matrix"].sum
    assert_equal  1,  data["result"][2]["matrix"].sum
  end
end