# Script to configure environment specific vars.
# 
# Execution format: 
# ruby vars.rb --domain <value>

require 'optparse'
require 'ostruct'

# parse command line args
@options = OpenStruct.new
OptionParser.new do |opts|
  opts.on('-domain','--domain') do |domain|
    @options.domain = domain
  end
end.parse!

def update_angularjs
  content = File.read(@angularjs)
  content.gsub!(@domain, @options.domain)
  File.open(@angularjs, 'w') { |file| file.write content }
end

@domain = /local.example.com/

@angularjs = '../public/javascripts/main.js'

update_angularjs

puts 'Done!'