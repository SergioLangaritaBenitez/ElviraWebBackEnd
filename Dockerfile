FROM ruby:3.1.2
RUN gem install sinatra
RUN gem install puma
COPY bayesiannet.rb main.rb node.rb /opt
EXPOSE 4567
ENTRYPOINT [ "ruby","/opt/main.rb", "-o", "0.0.0.0" ]