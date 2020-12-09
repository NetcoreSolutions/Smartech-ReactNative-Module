require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "smartech-reactnative-module"
  s.version      = package["version"]
  s.summary      = package["title"]
  s.description  = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/NetcoreSolutions/Smartech-ReactNative-Module.git", :tag => "v#{s.version}" }

  s.source_files = "ios/**/*.{h,c,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "Smartech-iOS-SDK"
end

