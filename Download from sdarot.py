
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import time

BASE_URL="https://sdarot.tv/watch/88"
driver = webdriver.Firefox()
driver.get(BASE_URL)

ele_seasons=driver.find_element_by_id("season").find_elements_by_css_selector("li")

for ele_season in ele_seasons:
    print("Downloading season " + ele_season.text)
    ele_season.click()
    for ele_ep in driver.find_element_by_id("episode").find_elements_by_css_selector("li"):
        print("Downlaod episode: " + ele_ep.text)
        ele_ep.click()

        # Waiting for the episode to load
        while (driver.find_element_by_id("waitTime").get_attribute("class") != "hidden"):
            print ("Waiting for the timer to end...")
            time.sleep(1)
        
        driver.find_element_by_id("proceed").click()
        video = driver.find_element_by_id("videojs_html5_api")
        driver.get_cookies()




