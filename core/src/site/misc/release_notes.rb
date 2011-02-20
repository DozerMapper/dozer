#
# Generates valid release notes section to be pasted into releasenotes.xml
# Should be supplied with exact Url location from SF issue tracker
#

require 'open-uri'
require 'rexml/document'

include REXML

#url = <<url
#http://sourceforge.net/tracker/?limit=75&func=&group_id=133517&atid=727368&assignee=&status=1&category=&artgroup=&keyword=&submitter=&artifact_id=&assignee=&status=2&category=&artgroup=1129554&submitter=&keyword=&artifact_id=&submit=Filter&mass_category=&mass_priority=&mass_resolution=&mass_assignee=&mass_artgroup=&mass_status=&mass_cannedresponse=
#url

url = <<url
http://sourceforge.net/tracker/?limit=25&func=&group_id=133517&atid=727371&assignee=&status=&category=&artgroup=&keyword=&submitter=&artifact_id=&assignee=&status=2&category=&artgroup=1335462&submitter=&keyword=&artifact_id=&submit=Filter
url

page = open(url, 'User-Agent' => 'Ruby-Wget').read
                     
tickets = []

page.each_line {|l|

  if l =~ /href.*func=detail.*?([0-9]+).*>(.*)<\/a>/
   # puts l
    id = $1
    name = $2
    l =~ /href="(.*)"/
    url = $1.gsub(/\&/, "&amp;")
    
    tickets.push([id, name, url])
  end

}

puts "<ul>"
tickets.each {|i|
  puts "<li>#{i[1]} <a href=\"https://sourceforge.net#{i[2]}\">link</a></li>"
}
puts "</ul>"
