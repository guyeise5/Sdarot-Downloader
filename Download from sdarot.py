BASE_URL="https://sdarot.tv/watch/88"

from selenium import webdriver
from selenium.webdriver.common.keys import Keys
driver = webdriver.Firefox()
driver.get(BASE_URL)

ele_seasons=driver.find_element_by_id("season").find_elements_by_css_selector("li")

for ele_season in ele_seasons:
    print("Downloading season " + ele_season.text)
    ele_season.click()
    for ele_ep in driver.find_element_by_id("episode").find_elements_by_css_selector("li"):
        print("Downlaod episode: " + ele_ep.text)
        ele_ep.click()
