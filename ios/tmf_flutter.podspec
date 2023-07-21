#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint tmf_flutter.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'tmf_flutter'
  s.version          = '0.0.1'
  s.summary          = 'A new Flutter project for tencent tmf mini program plugins.'
  s.description      = <<-DESC
A new Flutter project for tencent tmf mini program plugins.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*.{h,m}'
  s.public_header_files = 'Classes/**/*.{h}'
  s.dependency 'Flutter'
  s.dependency 'TMFMiniAppSDK'
  s.dependency 'TMFMiniAppExtScanCode'
  s.dependency 'TMFMiniAppExtMedia'

  s.platform = :ios, '11.0'

  # ***************************
  #s.preserve_path = 'Classes/TMFMiniAppSDKBridge/module.modulemap'
  # ***************************
  s.static_framework = true

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = {
          'DEFINES_MODULE' => 'YES',
          'OTHER_LDFLAGS' => '$(inherited) -force_load $(PODS_ROOT)/TMFMiniAppExtMedia/Libraries/libTMFMiniAppExtMedia.a -force_load $(PODS_ROOT)/TMFMiniAppExtScanCode/Libraries/libTMFMiniAppExtScanCode.a -force_load $(PODS_ROOT)/TMFMiniAppSDK/Libraries/libTMFMiniAppSDK.a',
          'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386'
  }
end
