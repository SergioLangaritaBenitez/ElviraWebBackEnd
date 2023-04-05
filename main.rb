require 'sinatra'
configure { set :server, :puma }

    
get '/' do
    "Hello World!222"
end

get '/calculateNaivesNet' do
    "here we do the calculation"
    net=BayesianNet.new(input)
    net.evaluateNet
    return net.getResult
    end