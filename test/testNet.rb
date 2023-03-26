ENV['APP_ENV'] = 'test'

require 'test/unit'
require 'rack/test'
require_relative  '../bayesiannet'

class NodeTest < Test::Unit::TestCase
  include Rack::Test::Methods

  def app
    Sinatra::Application
  end

 

  def test_creation_one_node
    info='{"nodes":[{"id":"a","state":["si","no"],"matrix":[[1,0]] }], "connections":[]}'
    net=BayesianNet.new(info)
    assert_equal [1,0], net.evaluateNet
  end

  def test_creation_two_node
    info='{"nodes":[
      {"id":"a","state":["si","no"],"matrix":[[0.7,0.3]]},
      {"id":"b","state":["a","b"],"matrix":[[0.1,0.25],[0.9,0.75]]}
      ], "connections":[{"from":"a","to":"b"}]}'
    net=BayesianNet.new(info)
    #net.evaluateNet
    assert_equal  1, net.evaluateNet.sum

  end
=begin
  def test_creation_with_parents
    info='{"id":"a"}'
    net=BayesianNet.new(info)

    #assert_equal 2, n2.getNumState

  end
  def test_creation_net
    info='{"id":"a"}'
    net=BayesianNet.new(info)

    #assert_equal 2, n2.getNumState

  end

  def test_calculate
  end
=end
end