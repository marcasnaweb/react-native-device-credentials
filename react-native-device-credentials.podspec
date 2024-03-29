require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-device-credentials"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-device-credentials
                   DESC
  s.homepage     = "https://github.com/marcasnaweb/react-native-device-credentials"
  s.license      = "MIT"
  s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "Bruno Almeida" => "bruno@marcasnaweb.com.br" }
  s.platforms    = { :ios => "11.3" }
  s.source       = { :git => "https://github.com/marcasnaweb/react-native-device-credentials.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
	
  # s.dependency "..."
end

