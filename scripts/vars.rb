# Script to configure sensitive application variables in the environment.
# 
# Execution format: 
# ruby vars.rb --local_client_id <value> --local_client_secret <value> --vm_client_id <value> --vm_client_secret <value>

require 'optparse'
require 'ostruct'

# parse command line args
@options = OpenStruct.new
OptionParser.new do |opts|
  opts.on('-local_client_id','--local_client_id') do |id|
    @options.local_client_id = id
  end
  opts.on('-local_client_secret','--local_client_secret') do |secret|
    @options.local_client_secret = secret
  end
  opts.on('-vm_client_id','--vm_client_id') do |id|
    @options.vm_client_id = id
  end
  opts.on('-vm_client_secret','--vm_client_secret') do |secret|
    @options.vm_client_secret = secret
  end
end.parse!

def update_local_google_vars
  @local_files.each do |f|
    content = File.read(f)
    content.gsub!(@google_client_id, @options.local_client_id)
    content.gsub!(@google_client_secret, @options.local_client_secret)
    File.open(f, 'w') { |file| file.write content }
  end
end

def update_vm_google_vars
  @vm_files.each do |f|
    content = File.read(f)
    content.gsub!(@google_client_id, @options.vm_client_id)
    content.gsub!(@google_client_secret, @options.vm_client_secret)
    File.open(f, 'w') { |file| file.write content }
  end
end

@google_client_id = /google_client_id/
@google_client_secret = /google_client_secret/ 

@local_files = ['../activator','../activator.bat']
@vm_files = ['../vagrant/jenkins-config.xml']

update_local_google_vars
update_vm_google_vars

puts 'Done!'