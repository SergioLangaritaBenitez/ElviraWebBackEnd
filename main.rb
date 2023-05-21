require 'sinatra'
require_relative 'bayesiannet'
configure { set :server, :puma }

    
get '/' do
    "Elvira Web Backend is Ready"
end

post '/calculateNaivesNet' do
    params = request.body.read
    net=BayesianNet.new(params)
    net.evaluateNet
    return net.getResult
end