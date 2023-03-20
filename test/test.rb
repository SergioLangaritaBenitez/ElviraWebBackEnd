ENV['APP_ENV'] = 'test'

require 'test/unit'
require 'rack/test'
require_relative  '../main'

class HelloWorldTest < Test::Unit::TestCase
  include Rack::Test::Methods

  def app
    Sinatra::Application
  end

  def test_it_says_hello_world
    get '/'
    assert last_response.ok?
    assert_equal 'Hello World!222', last_response.body
  end

  def test_it_says_hello_to_a_person
    get '/calculateNaivesNet'
    assert last_response.ok?
    assert_equal 'here we do the calculation', last_response.body
  end
end